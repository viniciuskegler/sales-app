import { PagedResponse } from "@core/model/pagination.model";

export type ProductResponse = PagedResponse<ProductDTO>;

export interface ProductDTO {
    id: number;
    title: string;
    description: string;
    price: number;
    thumbnail: string;
    discountPercentage: number;
}

export interface ReviewDTO {
    id: number;
    reviewerId: number;
    reviewerName: string;
    comment: string;
    rating: number;
    reviewDate: string;
}

export interface ProductDetailsDTO {
    id: number;
    title: string;
    description: string;
    price: number;
    discountPercentage: number;
    rating: number;
    stock: number;
    brand: string;
    sku: string;
    reviews: ReviewDTO[];
    thumbnail: string;
    images: string[];
}
