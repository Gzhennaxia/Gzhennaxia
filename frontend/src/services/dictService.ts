import DictCacheManager from './dictCacheManager';
import apiClient from '../utils/apiClient';
import { Dict, DictItem, PageResult, PageParams } from '../types/dict';

export const getDictDetail = async (dict_code: string): Promise<Dict> => {
    try {
        return await apiClient.get(`/dict/${dict_code}`);
    } catch (error) {
        console.error('Failed to fetch dict detail', error);
        throw error;
    }
};

export const getDictList = async (): Promise<Dict[]> => {
    try {
        const data: Dict[] = await apiClient.get('/dict');
        // 更新缓存
        data.forEach((dict: Dict) => {
            DictCacheManager.updateDict(dict);
        });
        return data;
    } catch (error) {
        console.error('Failed to fetch dict list', error);
        throw error;
    }
};

export const getDictPage = async (params: PageParams): Promise<PageResult<Dict>> => {
    try {
        return await apiClient.post('/dict/page', params);
    } catch (error) {
        console.error('Failed to fetch dict page', error);
        throw error;
    }
};

export const getDictItems = async (dict_code: string): Promise<DictItem[]> => {
    try {
        return await apiClient.get(`/api/dict/admin/${dict_code}/items`);
    } catch (error) {
        console.error('Failed to fetch dict items', error);
        throw error;
    }
};

export const createDict = async (data: Partial<Dict>): Promise<Dict> => {
    try {
        const response = await apiClient.post('/dict', data);
        const result = response.data;
        DictCacheManager.updateDict(result);
        return result;
    } catch (error) {
        console.error('Failed to create dict', error);
        throw error;
    }
};

export const updateDict = async (dict_code: string, data: Partial<Dict>): Promise<void> => {
    try {
        await apiClient.put(`/api/dict/admin/${dict_code}`, data);
        // 更新缓存
        const current = DictCacheManager.getDict(dict_code);
        if (current) {
            DictCacheManager.updateDict({ ...current, ...data } as Dict);
        }
    } catch (error) {
        console.error('Failed to update dict', error);
        throw error;
    }
};

export const deleteDict = async (dict_code: string): Promise<void> => {
    try {
        await apiClient.delete(`/dict/${dict_code}`);
        DictCacheManager.removeDict(dict_code);
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