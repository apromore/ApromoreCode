import graph from './graph';
import util from './util';
import search from './search';

let PDp = {};

[   graph,
    util,
    search
].forEach(function(props) {
    Object.assign(PDp, props);
});

zk.afterMount(function () {
    // Ap.pd.init();
    setTimeout(function () {
        try {
            Ap.pd.installComboitemHandlers();
        } catch (e) {
            // pass
        }
    }, 1000);
    zAu.send(new zk.Event(zk.Widget.$('$win'), 'onLoaded'));
});

document.addEventListener("click", (evt) => {
    const el = searchOptions[0];
    const input = searchInput[0];
    const target = evt.target;

    if (el !== target && input !== target) {
        updateSelectedNodes();
    }
});

export default PDp;