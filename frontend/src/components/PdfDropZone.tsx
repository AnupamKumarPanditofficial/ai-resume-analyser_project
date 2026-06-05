// frontend/src/components/PdfDropZone.tsx

import { useCallback, useRef, useState } from 'react';

interface PdfDropZoneProps {
  file: File | null;
  onFileSelect: (file: File | null) => void;
  disabled: boolean;
}

export default function PdfDropZone({
  file,
  onFileSelect,
  disabled,
}: PdfDropZoneProps) {
  const [isDragOver, setIsDragOver] = useState(false);
  const [rejectionMessage, setRejectionMessage] = useState<string | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const validateAndSetFile = useCallback(
    (selectedFile: File) => {
      if (selectedFile.type !== 'application/pdf') {
        setRejectionMessage(
          `"${selectedFile.name}" is not a PDF file. Only .pdf files are accepted.`
        );
        onFileSelect(null);
        return;
      }
      if (selectedFile.size > 5 * 1024 * 1024) {
        setRejectionMessage(
          `"${selectedFile.name}" is ${formatBytes(selectedFile.size)}. Maximum file size is 5 MB.`
        );
        onFileSelect(null);
        return;
      }
      setRejectionMessage(null);
      onFileSelect(selectedFile);
    },
    [onFileSelect]
  );

  const handleDragOver = useCallback(
    (e: React.DragEvent) => {
      e.preventDefault();
      if (!disabled) setIsDragOver(true);
    },
    [disabled]
  );

  const handleDragLeave = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    setIsDragOver(false);
  }, []);

  const handleDrop = useCallback(
    (e: React.DragEvent) => {
      e.preventDefault();
      setIsDragOver(false);
      if (disabled) return;

      const droppedFile = e.dataTransfer.files[0];
      if (droppedFile) {
        validateAndSetFile(droppedFile);
      }
    },
    [disabled, validateAndSetFile]
  );

  const handleClick = useCallback(() => {
    if (!disabled) {
      fileInputRef.current?.click();
    }
  }, [disabled]);

  const handleFileInputChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const selectedFile = e.target.files?.[0];
      if (selectedFile) {
        validateAndSetFile(selectedFile);
      }
      e.target.value = '';
    },
    [validateAndSetFile]
  );

  const handleRemoveFile = useCallback(
    (e: React.MouseEvent) => {
      e.stopPropagation();
      onFileSelect(null);
      setRejectionMessage(null);
    },
    [onFileSelect]
  );

  return (
    <div className="flex flex-col gap-3 h-full">
      <label className="flex items-center gap-2 text-sm font-semibold text-surface-900">
        <svg
          className="w-4 h-4 text-surface-400"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
          strokeWidth={2}
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"
          />
        </svg>
        Resume PDF
      </label>

      <div
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
        onDrop={handleDrop}
        onClick={handleClick}
        className={`
          flex-1 min-h-[300px] rounded-xl border-2 border-dashed bg-white
          flex flex-col items-center justify-center gap-4
          transition-all duration-200 cursor-pointer
          ${disabled ? 'opacity-50 cursor-not-allowed bg-surface-50' : ''}
          ${
            isDragOver
              ? 'border-primary-400 bg-primary-50 scale-[1.01]'
              : file
              ? 'border-emerald-300 bg-emerald-50'
              : 'border-surface-200 hover:border-surface-300 hover:bg-surface-50'
          }
        `}
      >
        <input
          ref={fileInputRef}
          type="file"
          accept=".pdf"
          onChange={handleFileInputChange}
          className="hidden"
          disabled={disabled}
        />

        {file ? (
          <div className="flex flex-col items-center gap-3 animate-scale-in">
            <div className="w-14 h-14 rounded-xl bg-white border border-emerald-200 flex items-center justify-center shadow-sm">
              <svg
                className="w-6 h-6 text-emerald-500"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
                strokeWidth={2}
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  d="M9 12.75L11.25 15 15 9.75M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                />
              </svg>
            </div>
            <div className="text-center">
              <p className="text-sm font-medium text-surface-900 truncate max-w-[250px]">
                {file.name}
              </p>
              <p className="text-xs text-surface-500 mt-1">
                {formatBytes(file.size)}
              </p>
            </div>
            {!disabled && (
              <button
                onClick={handleRemoveFile}
                className="text-xs font-medium text-surface-500 hover:text-red-500 transition-colors mt-2"
              >
                Remove file
              </button>
            )}
          </div>
        ) : (
          <div className="flex flex-col items-center gap-3">
            <div
              className={`w-14 h-14 rounded-xl flex items-center justify-center transition-colors duration-200 ${
                isDragOver
                  ? 'bg-primary-100 border border-primary-200'
                  : 'bg-white border border-surface-200 shadow-sm'
              }`}
            >
              <svg
                className={`w-6 h-6 transition-colors duration-200 ${
                  isDragOver ? 'text-primary-500' : 'text-surface-400'
                }`}
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
                strokeWidth={2}
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  d="M3 16.5v2.25A2.25 2.25 0 005.25 21h13.5A2.25 2.25 0 0021 18.75V16.5m-13.5-9L12 3m0 0l4.5 4.5M12 3v13.5"
                />
              </svg>
            </div>
            <div className="text-center">
              <p className="text-sm font-medium text-surface-900">
                {isDragOver ? 'Drop your PDF here' : 'Click or drag PDF to upload'}
              </p>
              <p className="text-xs text-surface-500 mt-1">
                Maximum file size: 5 MB
              </p>
            </div>
          </div>
        )}

        {rejectionMessage && (
          <div className="flex items-center gap-2 px-4 py-2 rounded-lg bg-red-50 border border-red-200 animate-fade-in mx-4">
            <svg
              className="w-4 h-4 text-red-500 flex-shrink-0"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
              strokeWidth={2}
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="M12 9v3.75m9-.75a9 9 0 11-18 0 9 9 0 0118 0zm-9 3.75h.008v.008H12v-.008z"
              />
            </svg>
            <p className="text-xs font-medium text-red-600">{rejectionMessage}</p>
          </div>
        )}
      </div>
    </div>
  );
}

function formatBytes(bytes: number): string {
  if (bytes < 1024) return `${bytes} B`;
  const kb = bytes / 1024;
  if (kb < 1024) return `${kb.toFixed(1)} KB`;
  const mb = kb / 1024;
  return `${mb.toFixed(1)} MB`;
}
