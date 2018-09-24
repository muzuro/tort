import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';

import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';
import { catchError, map, tap } from 'rxjs/operators';
import {University} from "./model/university";


const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable()
export class UniversityService {

  private universitiesUrl = 'api/universities';  // URL to web api

  constructor(
    private http: HttpClient) { }

  /** GET universities from the server */
  getUniversities(from: number, count: number): Observable<University[]> {
    let params = new HttpParams()
      .set("from", from.toString())
      .set("count", count.toString())
    return this.http.get<University[]>('api/universities', {params: params})
      .pipe(
        // tap(heroes => this.log(`fetched heroes`)),
        catchError(this.handleError('getUniversities', []))
      );
  }

  /**
   * Handle Http operation that failed.
   * Let the app continue.
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
   */
  private handleError<T> (operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead

      // TODO: better job of transforming error for user consumption
      // this.log(`${operation} failed: ${error.message}`);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }

}
