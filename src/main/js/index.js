/** @jsx h */

import 'nashorn-polyfill';

import preact from "preact";
import preactRenderToString from "preact-render-to-string";

const render = preactRenderToString;
const { h, Component } = preact;

const TestComponent = () => <div>Hello from Preact SSR!</div>;

window.renderOnServer = function () {
    return render(<TestComponent />);
};
