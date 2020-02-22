import { Job } from './job';
import { SystemEnvironment } from './system-environment';

export interface Benchmarker {
    address: string;
    systemEnvironment: SystemEnvironment;
    currentJob: Job;
}
