import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse  } from '@angular/common/http';
import { ApiService } from '../api.service';

@Component({
  selector: 'app-update-profile',
  templateUrl: './update-profile.component.html',
  styleUrl: './update-profile.component.css'
})
export class UpdateProfileComponent {
	constructor(private http: HttpClient, private apiService: ApiService) { }
	
	username: string = '';
	password: string = '';
	new_password: string = '';
	updateResponse: string = '';
	
	update() {
		  this.apiService.update_profile(this.username, this.password, this.new_password).subscribe(
				  response => {
				        // Extract token from the response
				        console.log('Response:', response);
				        const token: string = response.body.token;
				        if(token !=null){
				        // Use the token as needed
				        console.log('Updated');}
				        else{
				        	console.log('Update Failed');
				        }
				      },
				      error => {
				        console.error('Login failed!', error);
				      }
		  );
		}


}
