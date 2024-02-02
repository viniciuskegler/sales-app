import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatFormFieldModule } from '@angular/material/form-field';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { PasswordModule } from 'primeng/password';

import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { CommonModule } from '@angular/common';
import { ToolbarModule } from 'primeng/toolbar';
@NgModule({
  imports: [
    CommonModule,
    MatInputModule,
    MatFormFieldModule,
    InputTextModule,
    FormsModule,
    ButtonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatIconModule,
    PasswordModule,
    MatToolbarModule,
    ToolbarModule,
  ],
  exports: [
    CommonModule,
    MatInputModule,
    MatFormFieldModule,
    InputTextModule,
    FormsModule,
    ButtonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatIconModule,
    PasswordModule,
    MatToolbarModule,
    ToolbarModule,
  ],
})
export class SharedModule {}
