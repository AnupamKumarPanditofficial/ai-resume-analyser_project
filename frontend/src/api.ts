// frontend/src/api.ts

import type { AnalysisResult, PaginatedAnalyses, ErrorResponse } from './types';

/**
 * Base URL for the backend API.
 * Uses environment variable if provided (for Vercel), else defaults to localhost for local dev.
 */
const API_BASE = import.meta.env.VITE_API_URL || 'https://ai-resume-analyser-project.onrender.com/api/v1';

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    let errorBody: ErrorResponse;
    try {
      errorBody = await response.json();
    } catch {
      errorBody = {
        status: response.status,
        error: response.statusText,
        message: `Request failed with status ${response.status}: ${response.statusText}`,
        timestamp: new Date().toISOString(),
      };
    }
    throw errorBody;
  }
  return response.json();
}

export async function analyzeResume(
  file: File,
  jobDescription: string
): Promise<AnalysisResult> {
  const formData = new FormData();
  formData.append('resume', file);
  formData.append('jobDescription', jobDescription);

  const response = await fetch(`${API_BASE}/analyze`, {
    method: 'POST',
    body: formData,
  });

  return handleResponse<AnalysisResult>(response);
}

export async function getAnalyses(
  page: number = 0,
  size: number = 10
): Promise<PaginatedAnalyses> {
  const response = await fetch(
    `${API_BASE}/analyses?page=${page}&size=${size}`
  );
  return handleResponse<PaginatedAnalyses>(response);
}

export async function getAnalysisById(id: string): Promise<AnalysisResult> {
  const response = await fetch(`${API_BASE}/analyses/${id}`);
  return handleResponse<AnalysisResult>(response);
}

export async function deleteAnalysis(id: string): Promise<void> {
  const response = await fetch(`${API_BASE}/analyses/${id}`, {
    method: 'DELETE',
  });

  if (!response.ok) {
    let errorBody: ErrorResponse;
    try {
      errorBody = await response.json();
    } catch {
      errorBody = {
        status: response.status,
        error: response.statusText,
        message: `Delete failed with status ${response.status}`,
        timestamp: new Date().toISOString(),
      };
    }
    throw errorBody;
  }
}
