
export type OnlyOneActiveController = {
    registerMe: ( itemId: string, setInactive: ()=>void ) => void
    unregisterMe: ( itemId: string ) => void
    setActive: ( activeItemId: string ) => void
}

export function createOnlyOneActiveController(): OnlyOneActiveController {
    const items = new Map<string,()=>void>();

    function registerMe( itemId: string, setInactive: ()=>void ) {
        items.set(itemId, setInactive);
    }

    function unregisterMe( itemId: string ) {
        items.delete(itemId);
    }

    function setActive( activeItemId: string ) {
        items.forEach( (setInactive, itemId) => {
            if (setInactive && activeItemId!==itemId)
                setInactive();
        })
    }

    return {
        registerMe,
        unregisterMe,
        setActive
    }
}