// frontend/src/components/HistoryPanel.tsx

import { useEffect, useState } from 'react';
import { getAnalyses, deleteAnalysis } from '../api';
import type { PaginatedAnalyses, AnalysisResult } from '../types';

interface HistoryPanelProps {
  onSelect: (analysis: AnalysisResult) => void;
}

export default function HistoryPanel({ onSelect }: HistoryPanelProps) {
  const [data, setData] = useState<PaginatedAnalyses | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [page, setPage] = useState(0);

  const fetchHistory = async (p: number) => {
    setIsLoading(true);
    try {
      const result = await getAnalyses(p, 5);
      setData(result);
    } catch (error) {
      console.error('Failed to fetch history:', error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchHistory(page);
  }, [page]);

  const handleDelete = async (e: React.MouseEvent, id: string) => {
    e.stopPropagation();
    if (!window.confirm('Are you sure you want to delete this analysis?')) return;
    try {
      await deleteAnalysis(id);
      if (data?.content.length === 1 && page > 0) {
        setPage(page - 1);
      } else {
        fetchHistory(page);
      }
    } catch (error) {
      console.error('Failed to delete analysis:', error);
      alert('Failed to delete analysis. Please try again.');
    }
  };

  return (
    <div className="flex flex-col h-full bg-white w-80 shadow-2xl">
      <div className="p-5 border-b border-surface-200 flex items-center justify-between">
        <h2 className="text-lg font-bold text-surface-900 tracking-tight flex items-center gap-2">
          <svg className="w-5 h-5 text-surface-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          Recent Analyses
        </h2>
      </div>

      <div className="flex-1 overflow-y-auto p-4 space-y-4 scrollbar-thin">
        {isLoading ? (
          <div className="flex justify-center py-8">
            <div className="w-8 h-8 rounded-full border-2 border-surface-200 border-t-primary-600 animate-spin" />
          </div>
        ) : !data || data.content.length === 0 ? (
          <p className="text-sm text-surface-500 text-center py-8">
            No past analyses found.
          </p>
        ) : (
          data.content.map((item) => (
            <div
              key={item.id}
              onClick={() => onSelect(item)}
              className="clean-card p-4 cursor-pointer hover:border-primary-300 hover:bg-primary-50 transition-all group relative"
            >
              <button
                onClick={(e) => handleDelete(e, item.id)}
                className="absolute top-2 right-2 p-1.5 rounded-md text-surface-400 hover:text-red-600 hover:bg-red-50 opacity-0 group-hover:opacity-100 transition-all"
                title="Delete analysis"
              >
                <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                </svg>
              </button>
              <div className="flex items-center justify-between mb-2 pr-6">
                <span className="text-sm font-bold text-surface-900 truncate flex-1">
                  {item.fileName}
                </span>
              </div>
              <div className="flex items-center justify-between text-xs text-surface-500">
                <span>{new Date(item.analyzedAt).toLocaleDateString()}</span>
                <span className={`font-bold ${item.atsScore >= 75 ? 'text-emerald-600' : item.atsScore >= 50 ? 'text-amber-600' : 'text-red-600'}`}>
                  {item.atsScore}% ATS
                </span>
              </div>
            </div>
          ))
        )}
      </div>

      {data && data.totalPages > 1 && (
        <div className="p-4 border-t border-surface-200 flex justify-between items-center bg-surface-50">
          <button
            onClick={() => setPage((p) => Math.max(0, p - 1))}
            disabled={page === 0}
            className="p-1.5 rounded-lg text-surface-600 hover:text-surface-900 hover:bg-surface-200 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
            </svg>
          </button>
          <span className="text-xs font-bold text-surface-500">
            Page {page + 1} of {data.totalPages}
          </span>
          <button
            onClick={() => setPage((p) => Math.min(data.totalPages - 1, p + 1))}
            disabled={page === data.totalPages - 1}
            className="p-1.5 rounded-lg text-surface-600 hover:text-surface-900 hover:bg-surface-200 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
            </svg>
          </button>
        </div>
      )}
    </div>
  );
}
