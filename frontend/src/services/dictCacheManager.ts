import { DictType, DictItem } from '../types/dict';

class DictCacheManager {
    private memoryCache: Map<string, DictType> = new Map();
    private localStorageKey = 'dict_cache';

    constructor() {
        this.loadFromLocalStorage();
    }

    private loadFromLocalStorage() {
        const cachedData = localStorage.getItem(this.localStorageKey);
        if (cachedData) {
            try {
                const parsedData = JSON.parse(cachedData);
                Object.entries(parsedData).forEach(([code, dict]) => {
                    this.memoryCache.set(code, dict as DictType);
                });
            } catch (e) {
                console.error('Failed to parse cached dict data', e);
                localStorage.removeItem(this.localStorageKey);
            }
        }
    }

    private saveToLocalStorage() {
        const cacheObj = Object.fromEntries(this.memoryCache);
        localStorage.setItem(this.localStorageKey, JSON.stringify(cacheObj));
    }

    public getDict(code: string): DictType | undefined {
        // TODO: 实现从内存缓存获取字典
        return undefined;
    }

    public getBatch(codes: string[]): Map<string, DictType> {
        // TODO: 实现批量获取字典
        return new Map();
    }

    public updateDict(dict: DictType) {
        // TODO: 实现更新字典
    }

    public removeDict(code: string) {
        // TODO: 实现移除字典
    }
}

export default new DictCacheManager();