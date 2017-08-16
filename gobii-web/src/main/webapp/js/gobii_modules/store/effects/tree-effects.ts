import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/exhaustMap';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/take';
import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {Effect, Actions} from '@ngrx/effects';
import {of} from 'rxjs/observable/of';
import {TreeStructureService} from '../../services/core/tree-structure-service';
import * as treeNodeActions from '../actions/treenode-action'

@Injectable()
export class TreeEffects {
    @Effect()
    initTreeNodes$ = this.actions$
        .ofType(treeNodeActions.INIT)
        .map((action: treeNodeActions.InitAction) =>
            new treeNodeActions.LoadTreeNodeAction(this.treeStructureService.getInitialTree())
        );


    constructor(private actions$: Actions,
                private treeStructureService: TreeStructureService,
                private router: Router) {
    }
}

/*
* export class TreeEffects {
    @Effect()
    initTreeNodes$ = this.actions$
        .ofType(treeNodeActions.INIT)
        .map((action: Auth.Login) => action.payload)
        .exhaustMap(auth =>
            this.authService
                .login(auth)
                .map(user => new Auth.LoginSuccess({user}))
                .catch(error => of(new Auth.LoginFailure(error)))
        );
    constructor(
        private actions$: Actions,
        private treeStructureService: TreeStructureService,
        private router: Router
    ) {}
}

* */