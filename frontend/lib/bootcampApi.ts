import { api } from "./api";
import type {
  BootcampListParams,
  BootcampRequest,
  BootcampResponse,
  BootcampTrackRequest,
  BootcampTrackResponse,
  PageBootcampResponse,
} from "./types";

export const bootcampApi = {
  getBootcamps: (params: BootcampListParams = {}) => {
    const query = new URLSearchParams();
    if (params.trackType) query.set("trackType", params.trackType);
    if (params.operationType) query.set("operationType", params.operationType);
    if (params.techStack) query.set("techStack", params.techStack);
    if (params.keyword) query.set("keyword", params.keyword);
    if (params.page !== undefined) query.set("page", String(params.page));
    if (params.size !== undefined) query.set("size", String(params.size));
    if (params.sort) query.set("sort", params.sort);
    const qs = query.toString();
    return api.get<PageBootcampResponse>(`/bootcamps${qs ? `?${qs}` : ""}`);
  },

  getBootcamp: (id: number) => api.get<BootcampResponse>(`/bootcamps/${id}`),

  createBootcamp: (body: BootcampRequest) =>
    api.post<BootcampResponse>("/bootcamps", body),

  updateBootcamp: (id: number, body: BootcampRequest) =>
    api.put<BootcampResponse>(`/bootcamps/${id}`, body),

  deleteBootcamp: (id: number) => api.delete<void>(`/bootcamps/${id}`),

  addTrack: (id: number, body: BootcampTrackRequest) =>
    api.post<BootcampTrackResponse>(`/bootcamps/${id}/tracks`, body),

  updateTrack: (id: number, trackId: number, body: BootcampTrackRequest) =>
    api.put<BootcampTrackResponse>(`/bootcamps/${id}/tracks/${trackId}`, body),

  deleteTrack: (id: number, trackId: number) =>
    api.delete<void>(`/bootcamps/${id}/tracks/${trackId}`),
};
