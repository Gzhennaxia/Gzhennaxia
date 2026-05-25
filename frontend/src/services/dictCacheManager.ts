import { Dict } from '../types/dict';

class DictCacheManager {
    private memoryCache: Map<string, Dict> = new Map();
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
                    this.memoryCache.set(code, dict as Dict);
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

    public getDict(code: string): Dict | undefined {
        return this.memoryCache.get(code);
    }

    public getBatch(codes: string[]): Map<string, Dict> {
        const result = new Map<string, Dict>();
        codes.forEach((code) => {
            const dict = this.memoryCache.get(code);
            if (dict) {
                result.set(code, dict);
            }
        });
        return result;
    }

    public updateDict(dict: Dict) {
        this.memoryCache.set(dict.dictCode, dict);
        this.saveToLocalStorage();
    }

    public removeDict(code: string) {
        this.memoryCache.delete(code);
        this.saveToLocalStorage();
    }
}

export default new DictCacheManager();
