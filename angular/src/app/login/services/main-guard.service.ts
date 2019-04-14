import { Injectable } from '@angular/core';
import { CanActivate, CanActivateChild, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { HttpResponse } from 'src/app/common/entities/http-response';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { CurrentUserContainerService } from 'src/app/common/services/current-user-container.service';

@Injectable({
  providedIn: 'root'
})
export class MainGuardService implements CanActivate, CanActivateChild {
  private static readonly IS_LOGGING_URL = 'api/isUserLogging';
  constructor(private router: Router,
    private http: HttpClient,
    private currentUserContainerService: CurrentUserContainerService) { }
  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.loginCheck();
  }
  canActivateChild(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    return false;
  }
  private loginCheck(): Observable<boolean> {
    return this.loginCheckImpl().pipe(map(httpResponse => {
      if (httpResponse.code === 301) {
        this.router.navigate(['/web/login/external']);
        return false;
      } else {
        // save user info to user container
        this.currentUserContainerService.saveUserInfo(httpResponse.data);
        return true;
      }
    }));
  }
  private loginCheckImpl(): Observable<HttpResponse> {
    return this.http.get<HttpResponse>(MainGuardService.IS_LOGGING_URL);
  }
}
