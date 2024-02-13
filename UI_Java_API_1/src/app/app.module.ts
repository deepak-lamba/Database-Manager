import { NgModule } from '@angular/core';
import { BrowserModule, provideClientHydration } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { RegistrationComponent } from './registration/registration.component';
import { LogsComponent } from './logs/logs.component';
import { MessagesComponent } from './messages/messages.component';
import { UpdateProfileComponent } from './update-profile/update-profile.component';
import { RequestComponent } from './request/request.component';
import { HttpClientModule } from '@angular/common/http';
import { ApiService } from './api.service';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegistrationComponent,
    LogsComponent,
    MessagesComponent,
    UpdateProfileComponent,
    RequestComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule
  ],
  providers: [
    provideClientHydration(),
    ApiService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
