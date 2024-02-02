import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoginComponent } from './login.component';
import { SignUpComponent } from './signup/sign-up.component';
import { SharedModule } from '../core/shared-module';
import { RouterModule, Routes } from '@angular/router';


const routes: Routes = [
  {path:'', redirectTo: 'login', pathMatch: "full"},
  {path:'login', component: LoginComponent},
  {path:'login/signup', component: SignUpComponent},
];
@NgModule({
  imports: [
    RouterModule.forChild(routes),
    CommonModule,
    SharedModule
  ],
  declarations: [LoginComponent, SignUpComponent],
  exports: [
    LoginComponent,
    SignUpComponent,
    RouterModule
  ],
})
export class AuthModule {}
