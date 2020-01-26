import { OutputPropertyResult } from './output-property-result';
export interface OutputBenchmark {
    id: number;
    originalName: string;
    customName: string;
    description: string;
    groupId: number;
    results: OutputPropertyResult[];
}
