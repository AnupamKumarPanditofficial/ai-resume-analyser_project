// frontend/src/types.ts

/**
 * Application state machine — drives all UI rendering.
 * No ad-hoc boolean flags (isLoading, hasError, etc.).
 */
export type AppState = 'idle' | 'loading' | 'success' | 'error';

/**
 * A single bullet-point improvement suggestion from the AI.
 */
export interface BulletImprovement {
  /** The original resume bullet point, verbatim. */
  original: string;
  /** Rewritten version using STAR method with metrics where inferable. */
  suggested: string;
  /** One sentence explaining what was improved and why. */
  reason: string;
}

/**
 * The full analysis result returned by the backend.
 */
export interface AnalysisResult {
  /** MongoDB document _id, or "" if storage failed. */
  id: string;
  /** Original uploaded PDF filename. */
  fileName: string;
  /** PDF file size in bytes. */
  fileSizeBytes: number;
  /** Overall ATS alignment score (0–100). */
  atsScore: number;
  /** Skills/tools/terms present in both resume and JD. */
  matchedKeywords: string[];
  /** High-value JD terms absent from the resume. */
  missingKeywords: string[];
  /** 3 rewritten bullet points targeting the weakest ones. */
  bulletPointImprovements: BulletImprovement[];
  /** ISO-8601 server-side analysis timestamp. */
  analyzedAt: string;
}

/**
 * Paginated response from GET /api/v1/analyses.
 */
export interface PaginatedAnalyses {
  content: AnalysisResult[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

/**
 * Unified error response from the backend.
 */
export interface ErrorResponse {
  status: number;
  error: string;
  message: string;
  timestamp: string;
}
