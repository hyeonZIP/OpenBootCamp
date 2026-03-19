import { Suspense } from "react";
import { bootcampApi } from "@/lib/bootcampApi";
import type { OperationType, TrackType } from "@/lib/types";
import BootcampCard from "@/components/bootcamp/BootcampCard";
import BootcampFilter from "@/components/bootcamp/BootcampFilter";
import Pagination from "@/components/bootcamp/Pagination";
import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "부트캠프 탐색",
};

interface PageProps {
  searchParams: Promise<{
    trackType?: string;
    operationType?: string;
    techStack?: string;
    keyword?: string;
    page?: string;
  }>;
}

export default async function BootcampsPage({ searchParams }: PageProps) {
  const sp = await searchParams;

  const page = sp.page ? parseInt(sp.page, 10) : 0;

  let result = null;
  let error = false;

  try {
    const res = await bootcampApi.getBootcamps({
      trackType: sp.trackType as TrackType | undefined,
      operationType: sp.operationType as OperationType | undefined,
      keyword: sp.keyword,
      page,
      size: 12,
    });
    result = res.data;
  } catch {
    error = true;
  }

  return (
    <div className="mx-auto max-w-6xl px-4 py-12">
      {/* 헤더 */}
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-gray-900">부트캠프 탐색</h1>
        <p className="mt-1 text-sm text-gray-500">
          {result ? `총 ${result.page.totalElements}개의 부트캠프` : "부트캠프를 검색하세요"}
        </p>
      </div>

      {/* 필터 */}
      <Suspense>
        <BootcampFilter />
      </Suspense>

      {/* 에러 */}
      {error && (
        <div className="rounded-xl border border-red-200 bg-red-50 py-12 text-center text-sm text-red-500">
          데이터를 불러오는 중 오류가 발생했습니다.
        </div>
      )}

      {/* 결과 없음 */}
      {!error && result && result.content.length === 0 && (
        <div className="rounded-xl border border-dashed border-gray-300 py-20 text-center text-gray-400">
          조건에 맞는 부트캠프가 없습니다.
        </div>
      )}

      {/* 목록 */}
      {!error && result && result.content.length > 0 && (
        <>
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {result.content.map((bootcamp) => (
              <BootcampCard key={bootcamp.id} bootcamp={bootcamp} />
            ))}
          </div>

          <Suspense>
            <Pagination currentPage={result.page.number} totalPages={result.page.totalPages} />
          </Suspense>
        </>
      )}
    </div>
  );
}
