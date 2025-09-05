export interface Dict {
    id: number;
    dictCode: string;
    dictName: string;
    version: string;
    status: number;
    remark?: string;
    items?: DictItem[];
    dictItems?: DictItem[]; // 新增字段，用于接口参数
    createdTime: string;
    updatedTime: string;
}

export interface DictItem {
    id?: number; // 创建时可选
    dictCode: string;
    itemCode: string;
    itemName: string;
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