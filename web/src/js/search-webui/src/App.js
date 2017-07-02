import React, {Component} from "react";
import "./App.css";
import InputArea from "./InputArea";
import OutputArea from "./OutputArea";

class App extends Component {
  render() {
    return (<p>
            <InputArea/>
            <hr/>
            <OutputArea/>
        </p>
    );
  }
}

export default App;
