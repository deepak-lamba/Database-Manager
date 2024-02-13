import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { ApiService } from '../api.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})

export class LoginComponent {
	constructor(private http: HttpClient, private apiService: ApiService) { }
	
	username: string = '';
	password: string = '';

  // Example usage in an HTTP request
  login() {
	  this.apiService.login(this.username, this.password).subscribe(
      response => {
        // Extract token from the response
        console.log('Response:', response);
        const token: string = response.body.token;
        if(token !=null){
	        // Use the token as needed
	        console.log('Logged In');}
	        else{
	        	console.log('Login Failed');
	        }
      },
      error => {
        console.error('Login failed!', error);
      }
    );
  }
}
