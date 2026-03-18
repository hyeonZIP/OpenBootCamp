"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { bootcampApi } from "@/lib/bootcampApi";
import { ApiError } from "@/lib/api";

interface Props {
  bootcampId: number;
  bootcampName: string;
}

export default function DeleteBootcampButton({ bootcampId, bootcampName }: Props) {
  const router = useRouter();
  const [showConfirm, setShowConfirm] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleDelete = async () => {
    setDeleting(true);
    setError(null);
    try {
      await bootcampApi.deleteBootcamp(bootcampId);
      router.push("/bootcamps");
    } catch (err) {
      if (err instanceof ApiError) {
        setError(err.message);
      } else {
        setError("삭제 중 오류가 발생했습니다.");
      }
    } finally {
      setDeleting(false);
    }
  };

  return (
    <>
      <button
        type="button"
        onClick={() => setShowConfirm(true)}
        className="rounded-lg border border-red-200 px-4 py-2 text-sm font-medium text-red-600 hover:bg-red-50"
      >
        삭제
      </button>

      {showConfirm && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
          <div className="w-full max-w-sm rounded-xl bg-white p-6 shadow-lg">
            <h2 className="mb-2 text-base font-semibold text-gray-900">부트캠프 삭제</h2>
            <p className="mb-6 text-sm text-gray-600">
              <strong>{bootcampName}</strong>을(를) 삭제하시겠습니까?
              <br />
              이 작업은 되돌릴 수 없습니다.
            </p>
            {error && <p className="mb-4 text-sm text-red-500">{error}</p>}
            <div className="flex gap-3">
              <button
                type="button"
                onClick={() => {
                  setShowConfirm(false);
                  setError(null);
                }}
                disabled={deleting}
                className="flex-1 rounded-lg border border-gray-300 py-2 text-sm font-medium text-gray-600 hover:bg-gray-50 disabled:opacity-50"
              >
                취소
              </button>
              <button
                type="button"
                onClick={handleDelete}
                disabled={deleting}
                className="flex-1 rounded-lg bg-red-600 py-2 text-sm font-medium text-white hover:bg-red-700 disabled:opacity-50"
              >
                {deleting ? "삭제 중..." : "삭제"}
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
