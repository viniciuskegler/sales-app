import { Routes, RouterModule } from '@angular/router';
import { AuthModule } from './login/auth.module';


const routes: Routes = [
  {path: '',  loadChildren: () => AuthModule},
  
];

export const routing = RouterModule.forRoot(routes);
