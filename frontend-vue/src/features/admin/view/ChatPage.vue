<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue';
import * as Stomp from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { getJwtToken } from '@/services/auth-helper';
import { jwtDecode } from 'jwt-decode';
import apiClient from '@/services/api-client'; // Importeer de apiClient

interface ChatMessage {
  sender: string;
  content: string;
  timestamp: string;
}

const messages = ref<ChatMessage[]>([]);
const client = ref<Stomp.Client | null>(null);
const messageInput = ref('');
const isConnected = ref(false);
const error = ref<string | null>(null);
const currentUsername = ref('Anonymous');
const chatBoxRef = ref<HTMLElement | null>(null); // Ref voor de scrollpositie

const API_HOST = 'http://localhost:8080';
const WS_ENDPOINT = `/ws`;
const SUBSCRIPTION_TOPIC = '/topic/public';
const SEND_ENDPOINT = '/app/chat.send';


function extractUsername(token: string) {
  try {
    const decoded: any = jwtDecode(token);
    currentUsername.value = decoded.sub || decoded.username || 'Gebruiker';
  } catch (e) {
    currentUsername.value = 'Onbekend';
  }
}

// NIEUW: Functie om historische berichten op te halen
async function loadHistory() {
  try {
    // Maakt een REST call naar de nieuwe ChatHistoryController
    const history = await apiClient<ChatMessage[]>('/chat/history');
    messages.value = history;
    // Scroll na het laden
    scrollToBottom();
  } catch (e) {
    console.error("Fout bij het laden van chat historie:", e);
    // Zet de foutmelding op de pagina
    error.value = 'Kan chatgeschiedenis niet laden. Probeer opnieuw in te loggen.';
  }
}

// Zorgt ervoor dat de scrollbalk altijd onderaan staat
function scrollToBottom() {
  nextTick(() => {
    const chatBox = chatBoxRef.value;
    if (chatBox) {
      chatBox.scrollTop = chatBox.scrollHeight;
    }
  });
}

// Functie om verbinding te maken
const connect = () => {
  const token = getJwtToken();

  if (!token) {
    error.value = 'U moet ingelogd zijn om de chat te gebruiken en berichten te versturen.';
    return;
  }

  const WEBSOCKET_AUTH_URL = `${API_HOST}${WS_ENDPOINT}?token=${token}`;
  extractUsername(token);

  const socket = new SockJS(WEBSOCKET_AUTH_URL);

  const stompClient = new Stomp.Client({
    webSocketFactory: () => socket,

    connectHeaders: {
      'Authorization': `Bearer ${token}`,
    },

    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
  });

  stompClient.onConnect = (frame) => {
    isConnected.value = true;
    console.log('Connected: ' + frame);

    // Abonneer op het publieke topic
    stompClient.subscribe(SUBSCRIPTION_TOPIC, (message) => {
      const body: ChatMessage = JSON.parse(message.body);
      messages.value.push(body);
      // Scroll bij elk nieuw live bericht
      scrollToBottom();
    });
  };

  stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    isConnected.value = false;
    error.value = 'Fout bij verbinding of authenticatie. Probeer opnieuw in te loggen.';
  };

  stompClient.activate();
  client.value = stompClient;
};

const sendMessage = () => {
  if (!client.value || !messageInput.value.trim() || !isConnected.value) {
    return;
  }

  const chatMessage: ChatMessage = {
    sender: currentUsername.value,
    content: messageInput.value.trim(),
    timestamp: '',
  };

  client.value.publish({
    destination: SEND_ENDPOINT,
    body: JSON.stringify(chatMessage),
  });

  messageInput.value = '';
};

const disconnect = () => {
  if (client.value) {
    client.value.deactivate();
    isConnected.value = false;
  }
};

onMounted(() => {
  // FIX: Laad eerst de historie, en maak dan de verbinding
  loadHistory();
  connect();
});

onBeforeUnmount(() => {
  disconnect();
});
</script>

<template>
  <div class="chat-page max-w-4xl mx-auto p-4 bg-gray-100 rounded-lg shadow-xl flex flex-col h-[90vh]">
    <h1 class="text-3xl font-bold text-gray-800 mb-4 border-b pb-2">Partij Chatroom</h1>

    <div v-if="error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative mb-4">
      {{ error }}
    </div>

    <div class="status mb-4 text-center">
        <span :class="{'text-green-600': isConnected, 'text-red-600': !isConnected}" class="font-medium">
            Status: {{ isConnected ? `Verbonden als ${currentUsername}` : 'Verbinding mislukt...' }}
        </span>
    </div>

    <!-- Message Display Area (Ref toegevoegd voor scrollen) -->
    <div ref="chatBoxRef" class="message-area flex-1 bg-white p-4 rounded-lg border border-gray-300 overflow-y-auto space-y-3 mb-4">
      <div
        v-for="(msg, index) in messages"
        :key="index"
        class="message flex"
        :class="{
                'justify-end': msg.sender === currentUsername,
                'justify-start': msg.sender !== currentUsername
            }"
      >
        <div
          class="p-3 rounded-xl max-w-xs sm:max-w-md break-words shadow-sm"
          :class="{
                    'bg-blue-600 text-white': msg.sender === currentUsername,
                    'bg-gray-200 text-gray-800': msg.sender !== currentUsername
                }"
        >
          <div class="flex justify-between items-baseline mb-1">
                    <span class="font-bold text-sm" :class="{ 'text-blue-200': msg.sender === currentUsername }">
                        {{ msg.sender === currentUsername ? 'U' : msg.sender }}
                    </span>
            <span class="text-xs ml-2 opacity-75" :class="{ 'text-blue-200': msg.sender === currentUsername }">{{ msg.timestamp }}</span>
          </div>
          <p class="text-base mt-1">{{ msg.content }}</p>
        </div>
      </div>
    </div>

    <!-- Message Input -->
    <div class="input-area flex">
      <input
        v-model="messageInput"
        @keyup.enter="sendMessage"
        type="text"
        placeholder="Typ een bericht..."
        :disabled="!isConnected || !!error"
        class="flex-1 p-3 border border-gray-400 rounded-l-lg focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-200"
      />
      <button
        @click="sendMessage"
        :disabled="!isConnected || !messageInput.trim() || !!error"
        class="bg-blue-600 text-white p-3 rounded-r-lg hover:bg-blue-700 transition duration-150 disabled:bg-blue-300 font-semibold"
      >
        Verstuur
      </button>
    </div>
  </div>
</template>

<style scoped>
.chat-page {
  /* Gebruik h-[90vh] in template voor flex-col hoogte */
}
.message-area {
  min-height: 400px;
  height: 100%;
}
</style>
