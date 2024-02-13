import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams, HttpResponse  } from '@angular/common/http'
import { Observable } from 'rxjs';

@Injectable()

export class ApiService {
	
   backendUrl: string = 'http://127.0.0.1:8000';

  constructor(private http: HttpClient) {}
  
  login(username: string, password: string): Observable<HttpResponse<any>>  {
	    const url = this.backendUrl+'/';
	    const body = { username, password };
	    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
	    return this.http.post<any>(url, body, { headers, observe: 'response' });
	  }
  
  register(username: string, name: string, password: string, role: string): Observable<string> {
	    const url = this.backendUrl+'/registration';
	    const body = { username, name, password, role };
	    return this.http.post(url, body, { responseType: 'text' });
	  }
  
  getLogs(): Observable<HttpResponse<any>>   {
	    const url = this.backendUrl+'/logs';
	    const headers = new HttpHeaders().set('withCredentials', 'true');
	    return this.http.get<any>(url, { headers, observe: 'response'});
	  }
  
  getMessages(): Observable<HttpResponse<any>> {
	  const url = this.backendUrl+'/messages';
	  const headers = new HttpHeaders().set('withCredentials', 'true');
	  return this.http.get<any>(url, { headers, observe: 'response' });
  }
  
  getMyMessages(): Observable<HttpResponse<any>> {
	  const url = this.backendUrl+'/my/messages';
	  const headers = new HttpHeaders().set('withCredentials', 'true');
	  return this.http.get<any>(url,{ headers, observe: 'response' });
  }
  
  updateMessage(updatedMessage: any): Observable<HttpResponse<any>> {
	  const url = this.backendUrl+'/update/messages';
	  const headers = new HttpHeaders().set('withCredentials', 'true');
	  return this.http.post<any>(url, { headers, observe: 'response' });
  }
  
  update_profile(username: string, password: string, new_password: string): Observable<HttpResponse<any>> {
	  const url = this.backendUrl + '/update_profile';
	  const body = { username, password, new_password };
	  const headers = new HttpHeaders().set('withCredentials', 'true');
	  return this.http.post<any>(url, body, { headers, observe: 'response' });
	}
  
  execute_operations(query: string): Observable<HttpResponse<any>> {
	  const url = this.backendUrl + '/operations';
	  const body = { query };
	  const headers = new HttpHeaders().set('withCredentials', 'true');
	  return this.http.post<any>(url, body, { headers, observe: 'response' });
	}
  
  getuserList(): Observable<HttpResponse<any>> {
	    const url = this.backendUrl + '/users';
	    const headers = new HttpHeaders().set('withCredentials', 'true');
	    return this.http.get<any>(url, { headers, observe: 'response' });
	    }

  submitRequest(requestData: any) {
	    const url = this.backendUrl + '/request';
	    const headers = new HttpHeaders().set('withCredentials', 'true');
	    return this.http.post(url, headers, requestData);
	  }
}
