export interface DictType {
    id: number;
    dict_code: string;
    dict_name: string;
    version: string;
    status: number;
    remark?: string;
    items?: DictItem[];
    created_time: string;
    updated_time: string;
}

export interface DictItem {
    id: number;
    type_code: string;
    item_key: string;
    item_value: string;
    sort: number;
    status: number;
    remark?: string;
    created_time: string;
    updated_time: string;
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