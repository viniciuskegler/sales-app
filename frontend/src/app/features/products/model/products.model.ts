//TODO REMOVER PAGINAÇÃO DAQUI (ESTUDAR QUAL A MELHOR ABORDAGEM)
export interface ProductResponse {
    products: ProductDTO[]
    total: number
    skip: number
    limit: number
}

export interface ProductDTO {
    id: number
    title: string
    description: string
    price: number
    thumbnail: string
    images: string[]
}