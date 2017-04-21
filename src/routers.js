/**
 * oxchain ivoice app
 *
 *
 * Author: Jun
 * Date: 13/04/2017
 *
 */

import React from 'react';
import { Route, IndexRoute } from 'react-router';

import RequireAuth from './components/auth/require_auth';
import App from './components/app';
import Nav from './components/welcome';
import Signin from './components/auth/signin';
import Signout from './components/auth/signout';
import Auto from './components/auto';
import Apply from './components/apply';
import Signup from './components/auth/signup';
import ReimburseList from './components/reimburse_list';
import Reimburse from './components/reimburse';
import InvoiceList from './components/invoice_list';
import ReimburseMine from './components/reimburse_mine';

export default (
  <Route path="/" component={App} >
    <IndexRoute component={Nav}/>
    <Route path="signin" component={Signin} />
    <Route path="signout" component={Signout} />
    <Route path="auto" component={RequireAuth(Auto)} />
    <Route path="apply" component={Apply} />
    <Route path="signup" component={Signup} />
    <Route path="/reimburse/list/:page" component={RequireAuth(ReimburseList)} />
    <Route path="/reimburse/:id" component={RequireAuth(Reimburse)} />
    <Route path="/invoice/list/:page" component={RequireAuth(InvoiceList)} />
    <Route path="/reimburse/mine/list/:page" component={RequireAuth(ReimburseMine)} />
  </Route>
);