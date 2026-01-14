export interface PagedResponse<T> {
    content: NonNullable<T>[];
    page: Pageable;
}
export interface Pageable {
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
}
