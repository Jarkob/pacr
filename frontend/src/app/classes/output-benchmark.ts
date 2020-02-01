import { OutputPropertyResult } from './output-property-result';
export interface OutputBenchmark {
    id: number;
    customName: string;
    description: string;
    groupId: number;
    results: OutputPropertyResult[];
}
