import type { ChartOptions, TooltipItem } from 'chart.js';

// 1. Export the constants directly
export const PARTY_COLORS = [
  '#FF6347', '#4682B4', '#3CB371', '#FFD700', '#DA70D6',
  '#00CED1', '#FFA07A', '#9370DB', '#8B0000', '#00BFFF',
  '#A0522D', '#7FFF00', '#BA55D3', '#BDB76B', '#1E90FF',
  '#FF4500', '#20B2AA', '#800000', '#00FF00', '#DC143C',
  '#008080', '#D2691E', '#48D1CC', '#8B4513', '#6A5ACD',
  '#F08080', '#006400', '#C71585', '#556B2F', '#9932CC',
  '#A9A9A9', '#FF8C00', '#B0E0E6', '#CD5C5C', '#696969',
  '#2F4F4F', '#000080', '#FFB6C1', '#8FBC8F', '#4169E1',
  '#B8860B'
];

// 2. Export a function to generate options (allows dynamic titles)
export const getChartOptions = (electionId: string): ChartOptions<'bar'> => {
  return {
    responsive: true,
    maintainAspectRatio: false,
    indexAxis: 'y' as const,
    plugins: {
      legend: {
        display: false,
      },
      title: {
        display: true,
        text: `Nationale verkiezingsresultaten: Stempercentage & Zetels (${electionId})`,
        font: {
          size: 18
        }
      },
      tooltip: {
        callbacks: {
          label: function(context: TooltipItem<'bar'>) {
            const dataset = context.dataset as unknown as { votesData: number[], seatsData: number[] };
            const votes = dataset.votesData[context.dataIndex];
            const seats = dataset.seatsData[context.dataIndex];
            const percentage = context.formattedValue;

            const formattedVotes = votes.toLocaleString('nl-NL');

            return [
              ` Stempercentage: ${percentage}%`,
              ` Totaal Stemmen: ${formattedVotes}`,
              ` Aantal Zetels: ${seats}`
            ];
          },
          title: function(context: TooltipItem<'bar'>[]) {
            return context[0].label;
          }
        }
      }
    },
    scales: {
      x: {
        stacked: false,
        title: {
          display: true,
          text: 'Stempercentage (%)',
          align: 'center'
        },
        ticks: {
          callback: function(value) {
            return value + '%';
          }
        }
      },
      y: {
        stacked: false
      }
    }
  };
};
