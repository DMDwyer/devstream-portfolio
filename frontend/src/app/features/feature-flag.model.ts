export interface FeatureFlag {
    id?: number;
    flagKey: string;
    description?: string;
    enabled: boolean;
    variantsJson?: string; // JSON string representing variants
}