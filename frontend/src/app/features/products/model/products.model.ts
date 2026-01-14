import { PagedResponse } from "@core/model/pagination.model";

export type ProductResponse = PagedResponse<ProductDTO>;
export interface ProductDTO {
    id: number;
    title: string;
    description: string;
    price: number;
    thumbnail: string;
    images: string[];
}
