import Link from "next/link";

export default function HomePage() {
  return (
    <div className="mx-auto max-w-6xl px-4 py-16">
      {/* Hero */}
      <section className="mb-20 text-center">
        <h1 className="mb-4 text-4xl font-bold tracking-tight text-gray-900 sm:text-5xl">
          부트캠프 실력, 직접 확인하세요
        </h1>
        <p className="mb-8 text-lg text-gray-500">
          수강생 프로젝트와 멘토 코드리뷰를 투명하게 공개합니다.
          <br />
          마케팅이 아닌 데이터로 부트캠프를 선택하세요.
        </p>
        <div className="flex justify-center gap-3">
          <Link
            href="/bootcamps"
            className="rounded-lg bg-gray-900 px-5 py-2.5 text-sm font-medium text-white hover:bg-gray-700"
          >
            부트캠프 탐색하기
          </Link>
          <Link
            href="/projects"
            className="rounded-lg border border-gray-300 px-5 py-2.5 text-sm font-medium text-gray-700 hover:bg-gray-50"
          >
            프로젝트 보기
          </Link>
        </div>
      </section>

      {/* 통계 */}
      <section className="mb-20 grid grid-cols-3 divide-x divide-gray-200 rounded-xl border border-gray-200 bg-gray-50">
        {[
          { label: "등록 부트캠프", value: "0개" },
          { label: "수강생 프로젝트", value: "0개" },
          { label: "코드리뷰", value: "0건" },
        ].map((stat) => (
          <div key={stat.label} className="py-8 text-center">
            <p className="text-2xl font-bold text-gray-900">{stat.value}</p>
            <p className="mt-1 text-sm text-gray-500">{stat.label}</p>
          </div>
        ))}
      </section>

      {/* 최신 프로젝트 */}
      <section className="mb-16">
        <div className="mb-6 flex items-center justify-between">
          <h2 className="text-xl font-semibold text-gray-900">최신 프로젝트</h2>
          <Link href="/projects" className="text-sm text-gray-500 hover:text-gray-900">
            더보기 →
          </Link>
        </div>
        <div className="rounded-xl border border-dashed border-gray-300 py-16 text-center text-gray-400">
          아직 등록된 프로젝트가 없습니다.
        </div>
      </section>

      {/* 주목받는 부트캠프 */}
      <section>
        <div className="mb-6 flex items-center justify-between">
          <h2 className="text-xl font-semibold text-gray-900">주목받는 부트캠프</h2>
          <Link href="/bootcamps" className="text-sm text-gray-500 hover:text-gray-900">
            더보기 →
          </Link>
        </div>
        <div className="rounded-xl border border-dashed border-gray-300 py-16 text-center text-gray-400">
          아직 등록된 부트캠프가 없습니다.
        </div>
      </section>
    </div>
  );
}
