export interface CommitHistoryItem {
    commitHash: string;
    commitDate: Date;
    compared: boolean;
    authorDate: Date;
    entryDate: Date;
    commitMessage: string;
    compared: boolean;
    significant: boolean;
    globalError: boolean;
    globalErrorMessage: string;
}
