var sortingRelation;

export function getSortedList(list, relation){
    sortingRelation = relation;
    return mergeSort(list);
}

function mergeSort(list){

    if(list.length <= 1){
        return list;
    }

    const left = list.slice(0, Math.floor(list.length / 2));
    const right = list.slice(Math.floor(list.length / 2 ));

    return merge(mergeSort(left), mergeSort(right));
}

function merge(left, right){

    let result = [], leftIndex = 0, rightIndex = 0;

    while(leftIndex < left.length && rightIndex < right.length){

        //comparable from left and right list; less is 1 if leftComp is sorted before, 0 if sorted equal to and 1 if sorted after rightComp.
        let leftComp = left[leftIndex], rightComp = right[rightIndex], less;

        //Deciding which element shall be sorted first
        switch(sortingRelation){
            case 'attendeeName':
                //Sorts by name
                less = leftComp.name.localeCompare(rightComp.name);
                break;
            case "attendeeGroup":
                //Sorts by group -> function -> name
                less = leftComp.group.localeCompare(rightComp.group);
                if(less === 0){
                    less = leftComp.function.localeCompare(rightComp.function);
                    if(less === 0){
                        less = leftComp.name.localeCompare(rightComp.name);
                    }
                }
                break;
            case "attendeeFunction":
                //Sorts by function -> group -> name
                less = leftComp.function.localeCompare(rightComp.function);
                if(less === 0){
                    less = leftComp.group.localeCompare(rightComp.group);
                    if(less === 0){
                        less = leftComp.name.localeCompare(rightComp.name);
                    }
                }
                break;
            default:
                console.log("Invalid sorting relation " + sortingRelation);
                break;
        }

        if(less === -1){
            result.push(leftComp);
            leftIndex++;
        } else{
            result.push(rightComp);
            rightIndex++;
        }
    }

    //Appending the remaining list after sorting
    return result.concat(left.slice(leftIndex)).concat(right.slice(rightIndex));
}
