// frontend/src/App.tsx

import { useState } from 'react';
import type { AppState, AnalysisResult, ErrorResponse } from './types';
import { analyzeResume } from './api';

import JobDescriptionInput from './components/JobDescriptionInput';
import PdfDropZone from './components/PdfDropZone';
import AnalyzeButton from './components/AnalyzeButton';
import LoadingSpinner from './components/LoadingSpinner';
import ErrorCard from './components/ErrorCard';
import ScoreGauge from './components/ScoreGauge';
import KeywordGrid from './components/KeywordGrid';
import BulletImprovements from './components/BulletImprovements';
import HistoryPanel from './components/HistoryPanel';

export default function App() {
  const [appState, setAppState] = useState<AppState>('idle');
  const [file, setFile] = useState<File | null>(null);
  const [jobDescription, setJobDescription] = useState('');
  const [result, setResult] = useState<AnalysisResult | null>(null);
  const [error, setError] = useState<ErrorResponse | null>(null);
  const [isHistoryOpen, setIsHistoryOpen] = useState(false);

  const isValid =
    file !== null && jobDescription.length >= 50 && jobDescription.length <= 5000;

  const handleAnalyze = async () => {
    if (!file || !isValid) return;

    setAppState('loading');
    setError(null);

    try {
      const res = await analyzeResume(file, jobDescription);
      setResult(res);
      setAppState('success');
    } catch (err) {
      setError(err as ErrorResponse);
      setAppState('error');
    }
  };

  const handleReset = () => {
    setAppState('idle');
    setResult(null);
    setError(null);
  };

  const handleSelectHistory = (historyItem: AnalysisResult) => {
    setResult(historyItem);
    setAppState('success');
    setIsHistoryOpen(false);
  };

  return (
    <div className="flex h-screen overflow-hidden bg-surface-50">
      <main className="flex-1 flex flex-col h-full overflow-y-auto relative">
        {/* Header - Clean white background, simple border */}
        <header className="px-8 py-4 bg-white border-b border-surface-200 sticky top-0 z-10 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 rounded-lg bg-surface-900 flex items-center justify-center">
              <svg className="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
            </div>
            <h1 className="text-xl font-bold text-surface-900 tracking-tight">
              AI Resume Analyzer
            </h1>
          </div>
          <button
            onClick={() => setIsHistoryOpen(!isHistoryOpen)}
            className="btn-secondary flex items-center gap-2"
          >
            <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            History
          </button>
        </header>

        {/* Content Container */}
        <div className="max-w-6xl mx-auto w-full px-8 py-10 flex-1 flex flex-col">
          {appState === 'idle' && (
            <div className="flex-1 flex flex-col gap-8 animate-fade-in">
              <div className="text-center space-y-2 mb-4">
                <h2 className="text-3xl font-extrabold text-surface-900 tracking-tight">Compare your resume to the job description</h2>
                <p className="text-surface-500">Get an instant ATS score, missing keywords, and actionable bullet point rewrites.</p>
              </div>
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 flex-1">
                <PdfDropZone
                  file={file}
                  onFileSelect={setFile}
                  disabled={false}
                />
                <JobDescriptionInput
                  value={jobDescription}
                  onChange={setJobDescription}
                  disabled={false}
                />
              </div>
              <div className="max-w-md mx-auto w-full pb-8">
                <AnalyzeButton
                  isValid={isValid}
                  isLoading={false}
                  onClick={handleAnalyze}
                />
              </div>
            </div>
          )}

          {appState === 'loading' && (
            <div className="flex-1 flex items-center justify-center">
              <LoadingSpinner />
            </div>
          )}

          {appState === 'error' && error && (
            <div className="flex-1 flex items-center justify-center">
              <ErrorCard error={error} onRetry={handleReset} />
            </div>
          )}

          {appState === 'success' && result && (
            <div className="space-y-8 pb-12 animate-fade-in">
              <div className="flex justify-end">
                <button onClick={handleReset} className="btn-secondary flex items-center gap-2">
                   <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                  </svg>
                  New Analysis
                </button>
              </div>

              <div className="clean-card p-8 flex flex-col md:flex-row items-center gap-12 justify-center">
                <ScoreGauge score={result.atsScore} analysisId={result.id} />
                <div className="space-y-3 max-w-md text-center md:text-left">
                  <h2 className="text-2xl font-bold text-surface-900">Analysis Complete</h2>
                  <p className="text-surface-600 leading-relaxed">
                    We compared <strong className="text-surface-900">{result.fileName}</strong> against the provided job description. Review the keyword matching and suggested bullet point improvements below.
                  </p>
                </div>
              </div>

              <KeywordGrid
                matchedKeywords={result.matchedKeywords}
                missingKeywords={result.missingKeywords}
              />

              <BulletImprovements improvements={result.bulletPointImprovements} />
            </div>
          )}
        </div>
      </main>

      {/* History Slide-out Panel */}
      <div
        className={`fixed inset-y-0 right-0 z-50 transform transition-transform duration-300 ease-in-out ${
          isHistoryOpen ? 'translate-x-0' : 'translate-x-full'
        }`}
      >
        <div className="h-full flex shadow-2xl relative border-l border-surface-200">
          <button
            onClick={() => setIsHistoryOpen(false)}
            className="absolute top-4 -left-12 p-2 bg-white text-surface-500 hover:text-surface-900 rounded-l-xl border border-r-0 border-surface-200 shadow-md"
          >
            <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
            </svg>
          </button>
          <HistoryPanel onSelect={handleSelectHistory} />
        </div>
      </div>

      {isHistoryOpen && (
        <div
          className="fixed inset-0 bg-surface-900/20 backdrop-blur-sm z-40 lg:hidden transition-opacity"
          onClick={() => setIsHistoryOpen(false)}
        />
      )}
    </div>
  );
}
