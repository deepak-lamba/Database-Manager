import { Component } from '@angular/core';
import { ApiService } from '../api.service';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrl: './registration.component.css'
})
export class RegistrationComponent {
	
	constructor(private apiService: ApiService) { }
	name: string = '';
	username: string = '';
	password: string = '';
	role: string = '';
	loginResponse: string = '';

	  register() {
		  this.apiService.register(this.username, this.name, this.password, this.role).subscribe(
		    loginResponse => {
		      // Handle the login response as needed
		      console.log('Output Received:', loginResponse);
		    },
		    error => {
		      // Handle login error
		      console.error('Registration failed!', error);
		    }
		  );
		}
}
