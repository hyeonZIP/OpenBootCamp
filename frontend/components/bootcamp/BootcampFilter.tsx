"use client";

import { useRouter, useSearchParams } from "next/navigation";
import { useCallback } from "react";
import { TRACK_TYPE_LABEL, OPERATION_TYPE_LABEL } from "@/lib/types";
import type { TrackType, OperationType } from "@/lib/types";

const TRACK_TYPES = Object.entries(TRACK_TYPE_LABEL) as [TrackType, string][];
const OPERATION_TYPES = Object.entries(OPERATION_TYPE_LABEL) as [OperationType, string][];

export default function BootcampFilter() {
  const router = useRouter();
  const searchParams = useSearchParams();

  const current = {
    keyword: searchParams.get("keyword") ?? "",
    trackType: searchParams.get("trackType") ?? "",
    operationType: searchParams.get("operationType") ?? "",
  };

  const update = useCallback(
    (key: string, value: string) => {
      const params = new URLSearchParams(searchParams.toString());
      if (value) {
        params.set(key, value);
      } else {
        params.delete(key);
      }
      params.delete("page");
      router.push(`/bootcamps?${params.toString()}`);
    },
    [router, searchParams]
  );

  const reset = () => router.push("/bootcamps");

  const hasFilter = current.keyword || current.trackType || current.operationType;

  return (
    <div className="mb-8 flex flex-wrap items-center gap-3">
      {/* 키워드 검색 */}
      <input
        type="search"
        placeholder="부트캠프 이름 검색..."
        defaultValue={current.keyword}
        onChange={(e) => update("keyword", e.target.value)}
        className="h-9 rounded-lg border border-gray-300 px-3 text-sm outline-none focus:border-gray-500 focus:ring-1 focus:ring-gray-500"
      />

      {/* 트랙 유형 */}
      <select
        value={current.trackType}
        onChange={(e) => update("trackType", e.target.value)}
        className="h-9 rounded-lg border border-gray-300 px-3 text-sm outline-none focus:border-gray-500"
      >
        <option value="">트랙 유형 전체</option>
        {TRACK_TYPES.map(([value, label]) => (
          <option key={value} value={value}>
            {label}
          </option>
        ))}
      </select>

      {/* 운영 방식 */}
      <select
        value={current.operationType}
        onChange={(e) => update("operationType", e.target.value)}
        className="h-9 rounded-lg border border-gray-300 px-3 text-sm outline-none focus:border-gray-500"
      >
        <option value="">운영 방식 전체</option>
        {OPERATION_TYPES.map(([value, label]) => (
          <option key={value} value={value}>
            {label}
          </option>
        ))}
      </select>

      {/* 초기화 */}
      {hasFilter && (
        <button
          onClick={reset}
          className="h-9 rounded-lg border border-gray-300 px-3 text-sm text-gray-500 hover:bg-gray-50"
        >
          초기화
        </button>
      )}
    </div>
  );
}
