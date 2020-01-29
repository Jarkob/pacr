export interface DiagramOutputResult {
    commitHash: string;
    commitDate: string;
    authorDate: string;
    result: {
        result?: number,
        errorMessage?: string
    };
}
