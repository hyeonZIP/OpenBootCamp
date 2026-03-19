"use client";

import { useRouter, useSearchParams } from "next/navigation";

interface Props {
  currentPage: number;
  totalPages: number;
}

export default function Pagination({ currentPage, totalPages }: Props) {
  const router = useRouter();
  const searchParams = useSearchParams();

  if (totalPages <= 1) return null;

  const goTo = (page: number) => {
    const params = new URLSearchParams(searchParams.toString());
    params.set("page", String(page));
    router.push(`/bootcamps?${params.toString()}`);
  };

  const pages = Array.from({ length: totalPages }, (_, i) => i);
  const start = Math.max(0, currentPage - 2);
  const end = Math.min(totalPages - 1, currentPage + 2);
  const visible = pages.slice(start, end + 1);

  return (
    <div className="mt-10 flex items-center justify-center gap-1">
      <button
        onClick={() => goTo(currentPage - 1)}
        disabled={currentPage === 0}
        className="rounded-lg border border-gray-200 px-3 py-1.5 text-sm text-gray-500 hover:bg-gray-50 disabled:opacity-40"
      >
        이전
      </button>
      {visible.map((p) => (
        <button
          key={p}
          onClick={() => goTo(p)}
          className={`rounded-lg border px-3 py-1.5 text-sm ${
            p === currentPage
              ? "border-gray-900 bg-gray-900 text-white"
              : "border-gray-200 text-gray-600 hover:bg-gray-50"
          }`}
        >
          {p + 1}
        </button>
      ))}
      <button
        onClick={() => goTo(currentPage + 1)}
        disabled={currentPage === totalPages - 1}
        className="rounded-lg border border-gray-200 px-3 py-1.5 text-sm text-gray-500 hover:bg-gray-50 disabled:opacity-40"
      >
        다음
      </button>
    </div>
  );
}
