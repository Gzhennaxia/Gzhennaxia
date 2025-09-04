export interface Dict {
    id: number;
    dictCode: string;
    dictName: string;
    version: string;
    status: number;
    remark?: string;
    items?: DictItem[];
    createdTime: string;
    updatedTime: string;
}

export interface DictItem {
    id: number;
    dict_code: string;
    item_code: string;
    item_name: string;
    sort: number;
    status: number;
    remark?: string;
    createdTime: string;
    updatedTime: string;
}

export interface PageResult<T> {
    records: T[];
    total: number;
    size: number;
    current: number;
    pages: number;
}

export interface PageParams {
    pageNo: number;
    pageSize: number;
    query?: Record<string, any>;
    sort?: Record<string, 'asc' | 'desc'>;
}