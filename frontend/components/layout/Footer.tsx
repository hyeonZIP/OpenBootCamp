import Link from "next/link";

export default function Footer() {
  return (
    <footer className="border-t border-gray-200 bg-white">
      <div className="mx-auto max-w-6xl px-4 py-8">
        <div className="flex flex-col items-center justify-between gap-4 sm:flex-row">
          <p className="text-sm font-semibold text-gray-900">OpenBootCamp</p>
          <nav className="flex gap-6 text-sm text-gray-500">
            <Link href="/bootcamps" className="hover:text-gray-900">
              부트캠프 탐색
            </Link>
            <Link href="/projects" className="hover:text-gray-900">
              프로젝트
            </Link>
            <Link href="/compare" className="hover:text-gray-900">
              비교하기
            </Link>
          </nav>
          <p className="text-xs text-gray-400">
            © {new Date().getFullYear()} OpenBootCamp. All rights reserved.
          </p>
        </div>
      </div>
    </footer>
  );
}
