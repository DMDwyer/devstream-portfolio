import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FeatureFlag } from './feature-flag.model';

@Injectable({
  providedIn: 'root'
})
export class FeatureFlagsService {
  private readonly baseUrl = '/api/flags';

    constructor(private http: HttpClient) {}

    getAll(): Observable<FeatureFlag[]> {
        return this.http.get<FeatureFlag[]>(this.baseUrl);
    }

    toggle(id: number, enabled: boolean): Observable<FeatureFlag> {
        return this.http.patch<FeatureFlag>(`${this.baseUrl}/${id}`, { enabled });
    }

    create(flag: FeatureFlag): Observable<FeatureFlag> {
        return this.http.post<FeatureFlag>(this.baseUrl, flag);
    }

}