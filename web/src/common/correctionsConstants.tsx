export interface SizeCorrection {
  field: string,
  invertSize: string,
  fishSize: string
}

export const unsized: Array<SizeCorrection> = [
  {
    field: 'measurements.0',
    invertSize: '',
    fishSize: 'Unsized'
  }
];

export const measurements = Object.freeze([
  {
    field: 'measurements.1',
    invertSize: '0.5',
    fishSize: '2.5'
  },
  {
    field: 'measurements.2',
    invertSize: '1',
    fishSize: '5'
  },

  {
    field: 'measurements.3',
    fishSize: '7.5',
    invertSize: '1.5'
  },
  {
    field: 'measurements.4',
    fishSize: '10',
    invertSize: '2'
  },
  {
    field: 'measurements.5',
    fishSize: '12.5',
    invertSize: '2.5'
  },
  {
    field: 'measurements.6',
    fishSize: '15',
    invertSize: '3'
  },
  {
    field: 'measurements.7',
    fishSize: '20',
    invertSize: '3.5'
  },
  {
    field: 'measurements.8',
    fishSize: '25',
    invertSize: '4'
  },
  {
    field: 'measurements.9',
    fishSize: '30',
    invertSize: '4.5'
  },
  {
    field: 'measurements.10',
    fishSize: '35',
    invertSize: '5'
  },
  {
    field: 'measurements.11',
    fishSize: '40',
    invertSize: '5.5'
  },
  {
    field: 'measurements.12',
    fishSize: '50',
    invertSize: '6'
  },
  {
    field: 'measurements.13',
    fishSize: '62.5',
    invertSize: '6.5'
  },
  {
    field: 'measurements.14',
    fishSize: '75',
    invertSize: '7'
  },
  {
    field: 'measurements.15',
    fishSize: '87.5',
    invertSize: '7.5'
  },
  {
    field: 'measurements.16',
    fishSize: '100',
    invertSize: '8'
  },
  {
    field: 'measurements.17',
    fishSize: '112.5',
    invertSize: '8.5'
  },
  {
    field: 'measurements.18',
    fishSize: '125',
    invertSize: '9'
  },
  {
    field: 'measurements.19',
    fishSize: '137.5',
    invertSize: '9.5'
  },
  {
    field: 'measurements.20',
    fishSize: '150',
    invertSize: '10'
  },
  {
    field: 'measurements.21',
    fishSize: '162.5',
    invertSize: '10.5'
  },
  {
    field: 'measurements.22',
    fishSize: '175',
    invertSize: '11'
  },
  {
    field: 'measurements.23',
    fishSize: '187.5',
    invertSize: '11.5'
  },
  {
    field: 'measurements.24',
    fishSize: '200',
    invertSize: '12'
  },
  {
    field: 'measurements.25',
    fishSize: '250',
    invertSize: '12.5'
  },
  {
    field: 'measurements.26',
    fishSize: '300',
    invertSize: '13'
  },
  {
    field: 'measurements.27',
    fishSize: '350',
    invertSize: '13.5'
  },
  {
    field: 'measurements.28',
    fishSize: '400',
    invertSize: '14'
  }
] as Array<SizeCorrection>);

export const extendedMeasurements = Object.freeze([
  {
    field: 'measurements.29',
    fishSize: '450',
    invertSize: '14.5'
  },
  {
    field: 'measurements.30',
    fishSize: '500',
    invertSize: '15'
  },
  {
    field: 'measurements.31',
    fishSize: '550',
    invertSize: '16'
  },
  {
    field: 'measurements.32',
    fishSize: '600',
    invertSize: '17'
  },
  {
    field: 'measurements.33',
    fishSize: '650',
    invertSize: '18'
  },
  {
    field: 'measurements.34',
    fishSize: '700',
    invertSize: '19'
  },
  {
    field: 'measurements.35',
    fishSize: '750',
    invertSize: '20'
  },
  {
    field: 'measurements.36',
    fishSize: '800',
    invertSize: '22'
  },
  {
    field: 'measurements.37',
    fishSize: '850',
    invertSize: '24'
  },
  {
    field: 'measurements.38',
    fishSize: '900',
    invertSize: '26'
  },
  {
    field: 'measurements.39',
    fishSize: '950',
    invertSize: '28'
  },
  {
    field: 'measurements.40',
    fishSize: '1000',
    invertSize: '30'
  }
] as Array<SizeCorrection>);

export const allMeasurements = [...measurements, ...extendedMeasurements];
