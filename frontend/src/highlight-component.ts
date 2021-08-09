import {css, customElement, html, LitElement, property} from 'lit-element';

@customElement('highlight-component')
export class HighlightComponent extends LitElement {

    static get styles() {
        return css`
        :host {
            display: block;
        }
        .highlight {
            background-color: lightblue;
            transition: background-color 500ms linear;
        }
        .no-highlight {
            background-color: none;
            transition: background-color 500ms linear;
        }
        `;
    }

    @property({ attribute: true })
    private value: String = "";

    @property({ attribute: true })
    private highlight: Boolean = false;

    @property({ attribute: true })
    private initialized: Boolean = false;

    updated(changedProperties: Map<string, unknown>) {
        super.update(changedProperties);
        if (changedProperties.has("value") && this.initialized) {
            this.highlight = true;
            setTimeout(() => this.highlight = false, 500);
        }

        this.initialized = true;
    }

    render() {
        return html`<span class="${this.highlight?'highlight':'no-highlight'}">${this.value}</span>`;
    }
}
