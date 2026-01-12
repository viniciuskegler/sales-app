export interface PagedResponse<U>{
    content: U[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
}