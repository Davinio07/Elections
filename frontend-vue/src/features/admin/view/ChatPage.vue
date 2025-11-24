<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue';
import * as Stomp from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { getJwtToken } from '@/services/auth-helper';

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
const username = ref('Anon'); // Wordt overschreven door de server/token data

// Let op: Controleer of dit de juiste poort is die uw backend gebruikt (8080)
const API_HOST = 'http://localhost:8080';
const WEBSOCKET_URL = `${API_HOST}/ws`;
const SUBSCRIPTION_TOPIC = '/topic/public';
const SEND_ENDPOINT = '/app/chat.send';

// Functie om verbinding te maken
const connect = () => {
  const token = getJwtToken();

  if (!token) {
    error.value = 'U moet ingelogd zijn om de chat te gebruiken en berichten te versturen.';
    return;
  }

  // We gebruiken SockJS als fallback voor browsers die geen native WebSockets ondersteunen.
  const socket = new SockJS(WEBSOCKET_URL);

  const stompClient = new Stomp.Client({
    webSocketFactory: () => socket,

    // Stuur de JWT token als Authorization header
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

    // De server (ChatController) stelt de Principal in.
    // We kunnen de gebruikersnaam hier niet betrouwbaar van de server halen in een standaard STOMP connect,
    // maar de server zal de juiste naam instellen voordat het bericht wordt verstuurd.
    // Optioneel: u kunt de gebruikersnaam uit de JWT token parsen als u deze wilt tonen.

    // Abonneer op het publieke topic
    stompClient.subscribe(SUBSCRIPTION_TOPIC, (message) => {
      const body: ChatMessage = JSON.parse(message.body);
      messages.value.push(body);
    });
  };

  stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Details: ' + frame.body);
    isConnected.value = false;
    error.value = 'Fout bij verbinding of authenticatie. Probeer opnieuw in te loggen.';
  };

  stompClient.activate();
  client.value = stompClient;
};

// Functie om een bericht te versturen
const sendMessage = () => {
  if (!client.value || !messageInput.value.trim() || !isConnected.value) {
    console.warn('Niet verbonden of bericht is leeg.');
    return;
  }

  const chatMessage: ChatMessage = {
    // Sender is leeg, de server stelt de echte gebruikersnaam in
    sender: username.value,
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
  connect();
});

onBeforeUnmount(() => {
  disconnect();
});
</script>

<template>
  <div class="chat-page max-w-4xl mx-auto p-4 bg-gray-100 rounded-lg shadow-xl min-h-screen flex flex-col">
    <h1 class="text-3xl font-bold text-gray-800 mb-4 border-b pb-2">Partij Chatroom</h1>

    <div v-if="error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative mb-4">
      {{ error }}
    </div>

    <div class="status mb-4 text-center">
        <span :class="{'text-green-600': isConnected, 'text-red-600': !isConnected}" class="font-medium">
            Status: {{ isConnected ? `Verbonden` : 'Verbinding mislukt...' }}
        </span>
    </div>

    <!-- Message Display Area -->
    <div class="message-area flex-1 bg-white p-4 rounded-lg border border-gray-300 overflow-y-auto space-y-3 mb-4">
      <div
        v-for="(msg, index) in messages"
        :key="index"
        class="message p-3 rounded-lg max-w-xs sm:max-w-md break-words"
        :class="{
                'bg-blue-500 text-white ml-auto': msg.sender === 'Anonymous' ? false : msg.sender === username.value, // Controleert of het jouw bericht is
                'bg-gray-200 text-gray-800 mr-auto': msg.sender !== username.value
            }"
      >
        <div class="flex justify-between items-baseline mb-1">
                <span class="font-bold text-sm" :class="{ 'text-blue-200': msg.sender === username.value }">
                    {{ msg.sender === username.value ? 'U' : msg.sender }}
                </span>
          <span class="text-xs opacity-75 ml-2">{{ msg.timestamp }}</span>
        </div>
        <p class="text-base">{{ msg.content }}</p>
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
  height: 90vh;
}
.message-area {
  min-height: 400px;
  height: 100%;
}
</style>
