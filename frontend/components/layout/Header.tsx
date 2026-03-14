"use client";

import Link from "next/link";
import { useAuthStore } from "@/store/authStore";

export default function Header() {
  const { isAuthenticated, user, clearAuth } = useAuthStore();

  return (
    <header className="sticky top-0 z-50 w-full border-b border-gray-200 bg-white">
      <div className="mx-auto flex h-14 max-w-6xl items-center justify-between px-4">
        {/* 로고 */}
        <Link href="/" className="text-lg font-bold text-gray-900">
          OpenBootCamp
        </Link>

        {/* 네비게이션 */}
        <nav className="flex items-center gap-6 text-sm text-gray-600">
          <Link href="/bootcamps" className="hover:text-gray-900">
            부트캠프 탐색
          </Link>
          <Link href="/bootcamps/new" className="hover:text-gray-900">
            부트캠프 등록
          </Link>
          <Link href="/projects" className="hover:text-gray-900">
            프로젝트
          </Link>
          <Link href="/compare" className="hover:text-gray-900">
            비교하기
          </Link>
        </nav>

        {/* 인증 영역 */}
        <div className="flex items-center gap-3 text-sm">
          {isAuthenticated && user ? (
            <>
              <span className="text-gray-600">{user.username}</span>
              <Link href="/my" className="text-gray-600 hover:text-gray-900">
                마이페이지
              </Link>
              <button
                onClick={clearAuth}
                className="rounded-md bg-gray-100 px-3 py-1.5 text-gray-700 hover:bg-gray-200"
              >
                로그아웃
              </button>
            </>
          ) : (
            <Link
              href="/login"
              className="rounded-md bg-gray-900 px-3 py-1.5 text-white hover:bg-gray-700"
            >
              로그인
            </Link>
          )}
        </div>
      </div>
    </header>
  );
}
