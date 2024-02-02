import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LoginComponent implements OnInit {

  form: FormGroup;
  login:string = "";
  password:string = "";
  showPassword: boolean = false;


  constructor(private changeDetector: ChangeDetectorRef) {
    this.form = new FormGroup({ "loginControl": new FormControl("", [Validators.required, Validators.email]),  
      "passControl": new FormControl("", [Validators.required, Validators.minLength(8)])});
  }

  ngOnInit(): void {
    // timer(8000).pipe(take(1)).subscribe(() => {
    //   const teste = this.form;
    //   debugger
    // });
  }

  doLogin(){
    window.open("https://google.com.br", "_blank");
  }

  togglePassword(){
    debugger
    this.showPassword = !this.showPassword;
  }

  refreshView(){
   this.changeDetector.markForCheck();
  }

}
