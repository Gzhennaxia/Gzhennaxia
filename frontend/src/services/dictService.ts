import DictCacheManager from './dictCacheManager';
import apiClient from '../utils/apiClient';

interface DictType {
    id: number;
    code: string;
    name: string;
    version: string;
    status: number;
    remark?: string;
    items?: DictItem[];
}

interface DictItem {
    id: number;
    type_code: string;
    item_key: string;
    item_value: string;
    sort: number;
    status: number;
    remark?: string;
}

export const getDictList = async (): Promise<DictType[]> => {
    try {
        const response = await apiClient.get('/dict');
        // 更新缓存
        response.data.forEach((dict: DictType) => {
            DictCacheManager.updateDict(dict);
        });
        return response.data;
    } catch (error) {
        console.error('Failed to fetch dict list', error);
        throw error;
    }
};

export const getDictItems = async (code: string): Promise<DictItem[]> => {
    try {
        const response = await apiClient.get(`/api/dict/admin/${code}/items`);
        return response.data;
    } catch (error) {
        console.error('Failed to fetch dict items', error);
        throw error;
    }
};

export const createDict = async (data: Omit<DictType, 'id'>): Promise<DictType> => {
    try {
        const response = await apiClient.post('/api/dict/admin', data);
        DictCacheManager.updateDict(response.data);
        return response.data;
    } catch (error) {
        console.error('Failed to create dict', error);
        throw error;
    }
};

export const updateDict = async (code: string, data: Partial<DictType>): Promise<void> => {
    try {
        await apiClient.put(`/api/dict/admin/${code}`, data);
        // 更新缓存
        const current = DictCacheManager.getDict(code);
        if (current) {
            DictCacheManager.updateDict({ ...current, ...data });
        }
    } catch (error) {
        console.error('Failed to update dict', error);
        throw error;
    }
};

export const deleteDict = async (code: string): Promise<void> => {
    try {
        await apiClient.delete(`/api/dict/admin/${code}`);
        DictCacheManager.removeDict(code);
    } catch (error) {
        console.error('Failed to delete dict', error);
        throw error;
    }
};

export const createDictItem = async (dictCode: string, data: Omit<DictItem, 'id' | 'type_code'>): Promise<DictItem> => {
    try {
        const response = await apiClient.post(`/api/dict/admin/${dictCode}/items`, data);
        return response.data;
    } catch (error) {
        console.error('Failed to create dict item', error);
        throw error;
    }
};

export const updateDictItem = async (dictCode: string, itemId: number, data: Partial<DictItem>): Promise<void> => {
    try {
        await apiClient.put(`/api/dict/admin/${dictCode}/items/${itemId}`, data);
    } catch (error) {
        console.error('Failed to update dict item', error);
        throw error;
    }
};

export const deleteDictItem = async (dictCode: string, itemId: number): Promise<void> => {
    try {
        await apiClient.delete(`/api/dict/admin/${dictCode}/items/${itemId}`);
    } catch (error) {
        console.error('Failed to delete dict item', error);
        throw error;
    }
};