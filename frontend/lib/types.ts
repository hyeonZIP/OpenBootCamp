export type TrackType =
  | "BACKEND"
  | "FRONTEND"
  | "FULLSTACK"
  | "MOBILE"
  | "DATA"
  | "DEVOPS"
  | "AI_ML"
  | "SECURITY"
  | "GAME";

export type OperationType = "ONLINE" | "OFFLINE" | "HYBRID";

export type TechStack =
  | "JAVA" | "KOTLIN" | "PYTHON" | "NODE_JS" | "GO" | "RUBY"
  | "SPRING_BOOT" | "DJANGO" | "FLASK" | "FASTAPI" | "EXPRESS" | "NEST_JS"
  | "JAVASCRIPT" | "TYPESCRIPT" | "REACT" | "NEXT_JS" | "VUE" | "NUXT_JS" | "ANGULAR" | "SVELTE"
  | "ANDROID" | "IOS" | "REACT_NATIVE" | "FLUTTER"
  | "MYSQL" | "POSTGRESQL" | "MONGODB" | "REDIS" | "SQLITE" | "ORACLE"
  | "DOCKER" | "KUBERNETES" | "AWS" | "GCP" | "AZURE" | "CI_CD" | "LINUX" | "NGINX"
  | "PYTORCH" | "TENSORFLOW" | "SCIKIT_LEARN" | "PANDAS" | "SPARK"
  | "GRAPHQL" | "REST_API" | "WEBSOCKET" | "KAFKA" | "ELASTICSEARCH";

export interface BootcampTrackResponse {
  id: number;
  trackType: TrackType;
  operationType: OperationType;
  techStacks: TechStack[];
  priceMin: number | null;
  priceMax: number | null;
  durationWeeks: number | null;
  isRecruiting: boolean | null;
  createdAt: string;
  updatedAt: string;
}

export interface BootcampResponse {
  id: number;
  name: string;
  slug: string;
  logoUrl: string | null;
  description: string | null;
  officialUrl: string | null;
  tracks: BootcampTrackResponse[];
  createdAt: string;
  updatedAt: string;
}

export interface PageBootcampResponse {
  content: BootcampResponse[];
  page: {
    size: number;
    number: number;
    totalElements: number;
    totalPages: number;
  };
}

export interface BootcampListParams {
  trackType?: TrackType;
  operationType?: OperationType;
  techStack?: TechStack;
  keyword?: string;
  page?: number;
  size?: number;
  sort?: string;
}

export interface BootcampTrackRequest {
  trackType: TrackType;
  operationType: OperationType;
  techStacks?: TechStack[];
  priceMin?: number;
  priceMax?: number;
  durationWeeks?: number;
  isRecruiting?: boolean;
}

export interface BootcampRequest {
  name: string;
  englishName: string;
  logoUrl?: string;
  description?: string;
  officialUrl?: string;
  tracks?: BootcampTrackRequest[];
}

export const TRACK_TYPE_LABEL: Record<TrackType, string> = {
  BACKEND: "백엔드",
  FRONTEND: "프론트엔드",
  FULLSTACK: "풀스택",
  MOBILE: "모바일",
  DATA: "데이터",
  DEVOPS: "DevOps",
  AI_ML: "AI/ML",
  SECURITY: "보안",
  GAME: "게임",
};

export const OPERATION_TYPE_LABEL: Record<OperationType, string> = {
  ONLINE: "온라인",
  OFFLINE: "오프라인",
  HYBRID: "하이브리드",
};
