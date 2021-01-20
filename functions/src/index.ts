import * as functions from "firebase-functions"
import * as _ from "lodash"
import { DateTime, Duration } from "luxon"
import fetch from "node-fetch"

export const redirect = functions.https.onRequest( async ( request, response ) => {
    // 来月から試していく
    const date = DateTime.local().plus(Duration.fromObject({month: 1}))
    const url = await getLatestUrl( date, 3 )

    response.setHeader("Cache-Control", "public, max-age=604800")
    response.redirect( url, 307 )
} )

const getLatestUrl = async ( date: DateTime, count: number ): Promise<string> => {
    if ( count === 0 ) {
        throw new Error( "Too many failures." )
    }

    const year = date.year
    const month = _.padStart( date.month.toString(), 2, "0" )
    const url = `https://280blocker.net/files/280blocker_adblock_${year}${month}.txt`

    functions.logger.info( `Trying to check ${year}${month}. URL: ${url}` )
    const response = await fetch( url, { method: "HEAD" } )
    if ( response.ok ) {
        return url
    } else if ( response.status === 404 ) {
        functions.logger.info( `404. Tries the previous month.` )
        return getLatestUrl( date.minus( Duration.fromObject( { month: 1 } ) ), count - 1 )
    } else {
        functions.logger.error( `Trying to check ${year}${month}. URL: ${url}` )
        throw new Error( "Unexpected response." )
    }
}
