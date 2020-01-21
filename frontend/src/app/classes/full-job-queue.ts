import { Job } from './job';

export interface FullJobQueue {
    jobs: Job[];
    prioritizedJobs: Job[];
}
